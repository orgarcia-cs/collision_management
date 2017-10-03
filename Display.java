import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Display implements Runnable
{
	private int rows;
	private int columns;
	private int ObjectCount;

	public Display( int  r, int c, int oc) {
		ObjectCount = oc;
		rows = r;
		columns = c;
		ObjectCount = oc;
	}

	public void PrintBufferCD2Grid( int switch_bcd)
	{
		int count = 0;
		int x, y, id;
		int buffer_cd[][];

		if (switch_bcd == 0) {
			buffer_cd = Simulation.bufferc;
		} else {
			buffer_cd = Simulation.bufferd;
		}

		boolean collision_xy = false;
		boolean collision_xz = false;
		boolean collision_yz = false;
		boolean collision_xyz = false;
		boolean collision = false;

		count++;
		System.out.println( "TIME INTERVAL: " + Simulation.t.get());
		for( int i = 0; i < rows; i++) {
			for( int j = 0; j < columns; j++) {
				count = 0;
				for( int o = 0; o < ObjectCount; o++) {
					id = buffer_cd[o][0];
					x = buffer_cd[o][1];
					y = buffer_cd[o][2];
					if (( x == i) && (y == j)) {
						count += (1 << o);
					}
				}
				if( count == 0 ) {
					System.out.print( " [" + new String( new byte[] { (byte) 32}) + "] ");
				} else if( count == 1 ) {
					System.out.print( " [" + new String( new byte[] { 'x' }) + "] ");
				} else if ( count == 2) {
					System.out.print( " [" + new String( new byte[] { 'y' }) + "] ");
				} else if ( count == 3) {
					System.out.print( " [" + new String( new byte[] { 'x', 'y' }) + "]");
					collision_xy = true;
					collision = true;
				} else if ( count == 4) {
					System.out.print( " [" + new String( new byte[] { 'z' }) + "] ");
				} else if ( count == 5) {
					System.out.print( " [" + new String( new byte[] { 'x', 'z' }) + "]");
					collision_xz = true;
					collision = true;
				} else if ( count == 6) {
					System.out.print( " [" + new String( new byte[] { 'y', 'z' }) + "]");
					collision_yz = true;
					collision = true;
				} else if ( count == 7) {
					System.out.print( "[" + new String( new byte[] { 'x', 'y', 'z' }) + "]");
					collision_xyz = true;
					collision = true;
				} else {
					System.out.print( " [ ] ");
				}
			}
			System.out.println("");
		}
		if( !collision) {
			System.out.println("No Collision Detected.");
		}
		if ( collision_xy) {
			System.out.println("Collision Detected between 'x' and 'y'.");
		}
		if ( collision_xz) {
			System.out.println("Collision Detected between 'x' and 'y'.");
		}
		if ( collision_yz) {
			System.out.println("Collision Detected between 'x' and 'y'.");
		}
		if ( collision_xyz) {
			System.out.println("Collision Detected between 'x', 'y' and 'z'.");
		}
		return;
	}


// I know there is a simplification and overlap for using two semaphores, can do it with one
// but... too tired right now.
// Read appropriate buffer and write to display, in time with ticker from main process
	@Override
	public void run() {
		int flipbit = 0;
		while(Simulation.t.get() <= 10) {
			try {
				Simulation.SLockC.acquire();
				if( flipbit == 0) {
					PrintBufferCD2Grid( flipbit);
				} else {
					PrintBufferCD2Grid( flipbit);
				}
				flipbit = flipbit ^ 1;
				Simulation.SLockC.release();

				Thread.sleep(200);

				Simulation.STimeLock.acquire();
					System.out.println("DISPLAY: The Current time is " + Simulation.t.get() + "FLIPBIT: " + flipbit);
				Simulation.STimeLock.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

