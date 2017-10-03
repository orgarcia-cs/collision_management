import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.*;


public class Simulation {
	public static int buffera[][];
	public static int bufferb[][];
	public static int bufferc[][];
	public static int bufferd[][];
	public static AtomicInteger t;
	public static Semaphore SLockA;
	public static Semaphore SLockB;
	public static Semaphore SLockC;
	public static Semaphore SLockD;
	public static Semaphore STimeLock;

	public static void InitializeBuffer( int r, int c, int[][] buffer)
	{
		for(int i = 0; i < r; i++) {
			for( int j = 0; j < c; j++)  {
				buffer[i][j] = 48;
			}
		}
		buffer[0][0] = 48+1;
		buffer[0][2] = 48+2;
		buffer[3][6] = 48+4;
		return;
	}

	public static void InitializeVelocity( int o, int v[][], int dr, int dc)
	{
		v[o][0] = dr;
		v[o][1] = dc;
		return;
	}

	public static void main(String[] args) 
	{
		int rows = 8;
		int columns = 7;
		int vehicles = 3;
		int velocity[][] = new int[vehicles][2];

		SLockA = new Semaphore(1);
		SLockB = new Semaphore(1);
		SLockC = new Semaphore(1);
		SLockD = new Semaphore(1);
		STimeLock = new Semaphore(1);

		buffera = new int[rows][columns];
		bufferb = new int[rows][columns];
		bufferc = new int[vehicles][3];
		bufferd = new int[vehicles][3];
		t = new AtomicInteger(0);

		InitializeBuffer( rows, columns, buffera);
		InitializeBuffer( rows, columns, bufferb);


		InitializeVelocity( 0, velocity, 1, 1);
		InitializeVelocity( 1, velocity, 1, 0);
		InitializeVelocity( 2, velocity, 0, 1);

		Thread thread1 = new Thread( new UpdateAB( rows, columns, vehicles, velocity), "updateab");
		Thread thread2 = new Thread( new Display( rows, columns, vehicles ), "display");
		Thread thread3 = new Thread( new ReadAB2CD( rows, columns, vehicles ), "readab2cd");

		try{
			STimeLock.acquire();
			thread1.start();
			thread2.start();
			thread3.start();
			STimeLock.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		while( t.get() <= 11) {
			try {
				STimeLock.acquire();
				SLockA.acquire();
				SLockB.acquire();
				SLockC.acquire();
				Thread.sleep(100);
				SLockA.release();
				Thread.sleep(100);
				SLockB.release();
				Thread.sleep(100);
				SLockC.release();
				Thread.sleep(100);

            			Thread.sleep(700);

				t.getAndIncrement();

				STimeLock.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}



