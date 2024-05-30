package SevenZip;

public class Lzma
{
	static public class Commandos
	{
		public static final int kEncode = 0;
		public static final int kDecode = 1;
		public static final int kBenchmak = 2;
		
		public int Command = -1;
		public int NumBenchmarkPasses = 10;
		
		public int DictionarySize = 1 << 23;
		public boolean DictionarySizeIsDefined = false;
		
		public int Lc = 3;
		public int Lp = 0;
		public int Pb = 2;
		
		public int Fb = 128;
		public boolean FbIsDefined = false;
		
		public boolean Eos = false;
		
		public int Algorithm = 2;
		public int MatchFinder = 1;
		
		public String InFile;
		public String OutFile;
		
		boolean ParseSwitch(String s)
		{
			if (s.startsWith("d"))
			{
				DictionarySize = 1 << Integer.parseInt(s.substring(1));
				DictionarySizeIsDefined = true;
			}
			else if (s.startsWith("fb"))
			{
				Fb = Integer.parseInt(s.substring(2));
				FbIsDefined = true;
			}
			else if (s.startsWith("a"))
				Algorithm = Integer.parseInt(s.substring(1));
			else if (s.startsWith("lc"))
				Lc = Integer.parseInt(s.substring(2));
			else if (s.startsWith("lp"))
				Lp = Integer.parseInt(s.substring(2));
			else if (s.startsWith("pb"))
				Pb = Integer.parseInt(s.substring(2));
			else if (s.startsWith("eos"))
				Eos = true;
			else if (s.startsWith("mf"))
			{
				String mfs = s.substring(2);
				if (mfs.equals("bt2"))
					MatchFinder = 0;
				else if (mfs.equals("bt4"))
					MatchFinder = 1;
				else if (mfs.equals("bt4b"))
					MatchFinder = 2;
				else
					return false;
			}
			else
				return false;
			return true;
		}
		
		public boolean Parse(String[] args) throws Exception
		{
			int pos = 0;
			boolean switchMode = true;
			for (int i = 0; i < args.length; i++)
			{
				String s = args[i];
				if (s.length() == 0)
					return false;
				if (switchMode)
				{
					if (s.compareTo("--") == 0)
					{
						switchMode = false;
						continue;
					}
					if (s.charAt(0) == '-')
					{
						String sw = s.substring(1).toLowerCase();
						if (sw.length() == 0)
							return false;
						try
						{
							if (!ParseSwitch(sw))
								return false;
						}
						catch (NumberFormatException e)
						{
							return false;
						}
						continue;
					}
				}
				if (pos == 0)
				{
					if (s.equalsIgnoreCase("e"))
						Command = kEncode;
					else if (s.equalsIgnoreCase("d"))
						Command = kDecode;
					else if (s.equalsIgnoreCase("b"))
						Command = kBenchmak;
					else
						return false;
				}
				else if(pos == 1)
				{
					if (Command == kBenchmak)
					{
						try
						{
							NumBenchmarkPasses = Integer.parseInt(s);
							if (NumBenchmarkPasses < 1)
								return false;
						}
						catch (NumberFormatException e)
						{
							return false;
						}
					}
					else
						InFile = s;
				}
				else if(pos == 2)
					OutFile = s;
				else
					return false;
				pos++;
				continue;
			}
			return true;
		}
	}

}

