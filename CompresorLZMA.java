package SevenZip;

import SevenZip.Compression.LZMA.*;
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class CompresorLZMA {
    private static File inFile;
    private static File outFile;
    private static BufferedInputStream inStream;
    private static BufferedOutputStream outStream;
    private static final Lzma.Commandos params = new Lzma.Commandos();

    public CompresorLZMA(String entrada, String salida) throws Exception {
        inFile = new File(entrada);
        outFile = new File(salida);

        inStream = new BufferedInputStream(new java.io.FileInputStream(inFile));
        outStream = new BufferedOutputStream(new java.io.FileOutputStream(outFile));
    }

    public void comprimir() throws Exception {
        try {
            boolean eos = false;
            Encoder encoder = getEncoder(eos);
            encoder.WriteCoderProperties(outStream);
            long fileSize = inFile.length();
            for (int i = 0; i < 8; i++)
                outStream.write((int) (fileSize >>> (8 * i)) & 0xFF);
            encoder.Code(inStream, outStream, -1, -1, null);
        } catch (Exception e) {
            outStream.flush();
            outStream.close();
            inStream.close();

            throw new Exception(e.getMessage());
        }
        outStream.flush();
        outStream.close();
        inStream.close();
    }

    public void descomprimir() throws Exception {
        int propertiesSize = 5;
        byte[] properties = new byte[propertiesSize];
        if (inStream.read(properties, 0, propertiesSize) != propertiesSize)
            throw new Exception("input .lzma file is too short");
        SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
        if (!decoder.SetDecoderProperties(properties))
            throw new Exception("Incorrect stream properties");
        long outSize = 0;
        for (int i = 0; i < 8; i++) {
            int v = inStream.read();
            if (v < 0)
                throw new Exception("Can't read stream size");
            outSize |= ((long) v) << (8 * i);
        }
        if (!decoder.Code(inStream, outStream, outSize))
            throw new Exception("Error in data stream");

        outStream.flush();
        outStream.close();
        inStream.close();
    }

    protected static Encoder getEncoder(boolean eos) throws Exception {
        Encoder encoder = new Encoder();
        if (!encoder.SetAlgorithm(params.Algorithm))
            throw new Exception("Incorrect compression mode");
        if (!encoder.SetDictionarySize(params.DictionarySize))
            throw new Exception("Incorrect dictionary size");
        if (!encoder.SetNumFastBytes(params.Fb))
            throw new Exception("Incorrect -fb value");
        if (!encoder.SetMatchFinder(params.MatchFinder))
            throw new Exception("Incorrect -mf value");
        if (!encoder.SetLcLpPb(params.Lc, params.Lp, params.Pb))
            throw new Exception("Incorrect -lc or -lp or -pb value");
        encoder.SetEndMarkerMode(eos);
        return encoder;
    }

//    if(params.Command ==LzmaAlone.Commandos.kEncode ||params.Command ==LzmaAlone.Commandos.kDecode)
}
