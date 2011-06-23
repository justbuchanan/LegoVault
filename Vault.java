import josx.platform.rcx.*;



public class Vault
{
	private static final int DOT = 1;
	private static final int DASH = 2;
	private static final int RESET = 3;
	
	private static final double DOT_MIN = 0;
	private static final double DOT_MAX = 500;
	
	private static final double DASH_MIN = DOT_MAX;
	private static final double DASH_MAX = 2000;
	
	
	
	
	private static int key[];
	private static int entry[];
	private static int final keyLength;
	private static final int MAX_KEY_LENGTH = 10;
	
	private static final double LOCK_TIME = 3000;
	
	
	//private static final double ENTRY_TIMEOUT = 4000;
	
	
  public static void main (String[] aArg)
  throws Exception
  {
	  //	init key & entry arrays
	  keyLength = 4;
	  key = new int[keyLength];
	  entry = new int[20];
	  
	  key[0] = DOT;
	  key[1] = DASH;
	  key[2] = DOT;
	  key[3] = DASH;
	  
	  
	  
	  while ( true )
	  {
		  while ( isClosed() )
		  {
			  int entryIndex = 0;
			  
			  while ( entryIndex < MAX_KEY_LENGTH )
			  {
				  int character = getCharacter2();
				  
				  if ( character == RESET )break;
				  entry[entryIndex] = character;
				  
				  if ( checkEntry(entryIndex + 1) )
				  {
					  unlock();
					  break;
				  }
				  
				  ++entryIndex;
			  }
			  
			  if ( entryIndex == (MAX_KEY_LENGTH - 1) && isClosed() )
			  {
				  //	failed entry
				  Sound.buzz();
			  }
		  }
		  
		  lock();
	  }
	  
	
	//LCD.clear();
     //TextLCD.print ("hello");
     //Thread.sleep(20000);
     //TextLCD.print ("world");
     //Thread.sleep(2000);
  }
	
	
	public static boolean checkEntry(int entryCount)
	{
		if ( entryCount == keyLength )
		{
			for ( int i = 0; i < keyLength; i++ )
			{
				if ( entry[i] != key[i] )
				{
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
	
	
	
	public static double currentTime()
	{
		return (double)System.currentTimeMillis();
	}
	
	
	public static int getCharacter()
	{
		while ( !isPressed() ) {}	//	wait for them to release it if necessary
		double startTime = currentTime();
		while ( isPressed() ) {}	//	wait for press
		double pressDuration = currentTime() - startTime;
		
		if ( pressDuration > DOT_MIN && pressDuration < DOT_MAX ) return DOT;
		if ( pressDuration > DASH_MIN && pressDuration < DASH_MAX ) return DASH;
		return RESET;
	}
	
	
	public static int getCharacter2()
	{
		boolean dotAlert = false, dashAlert = false, resetAlert = false;
		
		while ( !isPressed() ) {}
		double startTime = currentTime();
		while ( isPressed() )
		{
			double pressDuration = currentTime() - startTime;
			if ( pressDuration > DOT_MIN && pressDuration < DOT_MAX && !dotAlert )
			{
				Sound.beep();
				dotAlert = true;
			}
			else if ( pressDuration > DASH_MIN && pressDuration < DASH_MAX && !dashAlert )
			{
				Sound.twoBeeps();
				dashAlert = true;
			}
			else if ( !resetAlert && pressDuration > DASH_MAX )
			{
				Sound.buzz();
				resetAlert = true;
			}
		}
		
		double pressDuration = currentTime() - startTime;
		if ( pressDuration > DOT_MIN && pressDuration < DOT_MAX ) return DOT;
		if ( pressDuration > DASH_MIN && pressDuration < DASH_MAX ) return DASH;
		return RESET;
	}
	
	
	
	public static void lock()
	throws Exception
	{
		while ( true )
		{
			while ( !isClosed() ) {}
			double startTime = currentTime();
			Motor.A.forward();
			
			while ( isClosed() )
			{
				double now = currentTime();
				if ( now - startTime > LOCK_TIME )
				{
					Motor.A.stop();
					Sound.beepSequence();
					return;
				}
			}
			Motor.A.stop();
		}
	}
	
	public static void unlock()
	throws Exception
	{
		Motor.A.backward();
		while ( isClosed() ) {}
		
		Thread.sleep(1800);
		Motor.A.stop();
	}
	
	
	public static boolean isPressed()
	{
		return Sensor.S2.readValue() > 0;
	}
	
	
	public static boolean isClosed()
	{
		return Sensor.S1.readValue() > 0;
	}
}

