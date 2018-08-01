import josx.platform.rcx.*;

public class Vault {
  private static final double INCREMENT_PAUSE = 410;

  private static final double ENTRY_TIMEOUT = 15000;

  private static int key[];
  private static int entry[];
  private static final int keyLength = 4;
  private static final int MAX_KEY_LENGTH = 10;

  private static final double LOCK_TIME = 3000;

  public static void main(String[] aArg) throws Exception {
    //	init key & entry arrays
    key = new int[keyLength];
    entry = new int[keyLength];

    key[0] = 5;
    key[1] = 6;
    key[2] = 2;
    key[3] = 9;

    while (true) {
      while (isClosed()) {
        wipeEntry();

        while (!isPressed()) {
        }
        while (isPressed()) {
        }

        int entryIndex = 0;

        double lastEntry = currentTime();
        double lastIncrement = currentTime();

        entry[entryIndex] = 0;

        showEntry(0);

        while (true) {
          if (currentTime() - lastIncrement > INCREMENT_PAUSE) {
            int value = (entry[entryIndex] + 1) % 10;
            entry[entryIndex] = value;
            lastIncrement = currentTime();

            showEntry(entryIndex + 1);
          }

          /*if ( currentTime() - lastEntry > ENTRY_TIMEOUT )
          {
                  Sound.buzz();
                  break;
          }*/

          if (isPressed()) {
            ++entryIndex;
            Sound.beep();
            while (isPressed()) {
            }
            lastIncrement = currentTime();
            lastEntry = lastIncrement;

            if (entryIndex == keyLength) {
              LCD.clear();
              TextLCD.print("");
              if (checkEntry()) {
                unlock();
                break;
              } else {
                LCD.clear();
                TextLCD.print("Oops...");
                Sound.buzz();
                break;
              }
            }
          }
        }
      }

      lock();
    }

    // LCD.clear();
    // TextLCD.print ("hello");
    // Thread.sleep(20000);
    // TextLCD.print ("world");
    // Thread.sleep(2000);
  }

  public static boolean checkEntry() {
    for (int i = 0; i < keyLength; i++) {
      if (entry[i] != key[i]) {
        return false;
      }
    }

    return true;
  }

  public static char charFromDigit(int digit) {
    char[] digitChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    int index = digit % 10;
    return digitChars[index];
  }

  public static void wipeEntry() {
    for (int i = 0; i < keyLength; i++) {
      entry[i] = -1;
    }
  }

  public static void showEntry(int charCount) {
    char[] disp = new char[keyLength];
    for (int i = 0; i < keyLength; i++) {
      if (i < charCount - 1) {
        disp[i] = '-';
      } else {
        int digit = entry[i];
        if (digit == -1)
          disp[i] = '_';
        else
          disp[i] = charFromDigit(digit);
      }
    }

    String dispStr = new String(disp, 0, keyLength);

    LCD.clear();
    TextLCD.print(dispStr);
  }

  public static double currentTime() {
    return (double)System.currentTimeMillis();
  }

  public static void lock() throws Exception {
    LCD.clear();
    TextLCD.print("Locking");

    while (true) {
      while (!isClosed()) {
      }
      double startTime = currentTime();
      Motor.A.forward();

      while (isClosed()) {
        double now = currentTime();
        if (now - startTime > LOCK_TIME) {
          Motor.A.stop();
          LCD.clear();
          TextLCD.print("Locked");
          Sound.beepSequence();
          return;
        }
      }
      Motor.A.stop();
    }
  }

  public static void unlock() throws Exception {
    LCD.clear();
    TextLCD.print("Opening");

    Motor.A.backward();
    while (isClosed()) {
    }

    Thread.sleep(1700);
    Motor.A.stop();
    LCD.clear();
    TextLCD.print("Open");
  }

  public static boolean isPressed() { return Sensor.S2.readValue() > 0; }

  public static boolean isClosed() { return Sensor.S1.readValue() > 0; }
}
