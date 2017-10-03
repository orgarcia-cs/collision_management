import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateAB implements Runnable {
	private int planes[][];
	private int rows;
	private int columns;
	private int ObjectCount;
	private int velocity[][];

	public UpdateAB( int r, int c, int oc, int v[][]) {
		planes = new int[oc][3];
		rows = r;
		columns = c;
		ObjectCount = oc;
		velocity = v;
	}

	public void PrintBufferAB()
	{
		for( int i = 0; i < rows; i++) {
			for( int j = 0; j < columns; j++) {
				System.out.print( new String( new byte[] { (byte) Simulation.buffera[i][j]}));
			}
			System.out.print("    ");
			for( int j = 0; j < columns; j++) {
				System.out.print( new String( new byte[] { (byte) Simulation.bufferb[i][j]}));
			}
			System.out.println("");
		}
		return;
	}
	
	public void UpdateBufferAB( int bab_switch )
	{
		String object;
		int count;
		int buffer_ab[][];

		PrintBufferAB();
		if( bab_switch == 0) {
			buffer_ab = Simulation.buffera;		// read from BufferA, write to bufferB this time
		} else {
			buffer_ab = Simulation.bufferb;		// read from BufferB, write to BufferA this time
		}
// Read the BufferA (0) or BufferB(1) and extract the locations of the vehicles on the buffer
// Then increment them according the the hardcoded [too many frickin' variables as it is] velocities above
// I don't know why I called the location arrays "planes", I guess to keep them separate from BufferCD  naming
// and no, I don't want to use BufferCD for this storage, Process UpdateBufferAB is not supposed to have access
// to Display(CD's) values
		for(int i = 0; i < rows; i++) {
			for( int j = 0; j < columns; j++) {
				count = buffer_ab[i][j] - 48;
				if( count == 1 ) {
					planes[0][0] = 'x';
					planes[0][1] = (i+velocity[0][0])%rows;
					planes[0][2] = (j+velocity[0][1])%columns;
				} else if ( count == 2) {
					planes[1][0] = 'y';
					planes[1][1] = (i+velocity[1][0])%rows;
					planes[1][2] = (j+velocity[1][1])%columns;
				} else if ( count == 3) {
					planes[0][0] = 'x';
					planes[0][1] = (i+velocity[0][0])%rows;
					planes[0][2] = (j+velocity[0][1])%columns;

					planes[1][0] = 'y';
					planes[1][1] = (i+velocity[1][0])%rows;
					planes[1][2] = (j+velocity[1][1])%columns;
				} else if ( count == 4) {
					planes[2][0] = 'z';
					planes[2][1] = (i+velocity[2][0])%rows;
					planes[2][2] = (j+velocity[2][1])%columns;
				} else if ( count == 5) {
					planes[0][0] = 'x';
					planes[0][1] = (i+velocity[0][0])%rows;
					planes[0][2] = (j+velocity[0][1])%columns;

					planes[2][0] = 'z';
					planes[2][1] = (i+velocity[2][0])%rows;
					planes[2][2] = (j+velocity[2][1])%columns;
				} else if ( count == 6) {
					planes[1][0] = 'y';
					planes[1][1] = (i+velocity[1][0])%rows;
					planes[1][2] = (j+velocity[1][1])%columns;

					planes[2][0] = 'z';
					planes[2][1] = (i+velocity[2][0])%rows;
					planes[2][2] = (j+velocity[2][1])%columns;
				} else if ( count == 7) {
					planes[0][0] = 'x';
					planes[0][1] = (i+velocity[0][0])%rows;
					planes[0][2] = (j+velocity[0][1])%columns;

					planes[1][0] = 'y';
					planes[1][1] = (i+velocity[1][0])%rows;
					planes[1][2] = (j+velocity[1][1])%columns;

					planes[2][0] = 'z';
					planes[2][1] = (i+velocity[2][0])%rows;
					planes[2][2] = (j+velocity[2][1])%columns;
				} else {
					;
				}
			}
		}
// That computationally expensive task done (I mean it is like -> O(n^3))
// Do the same thing again in O(n^3) space to repopulate the buffer with icons
// representing 0,1 or 2 vehicles in a space after they have been moved by the loop(s) above
// But I mean, the idea here is a robot eye is observing a real phenomena (trains on segment of land
// being observed by an overhead camera) and has
// to encode it into an array definining the locations of the vehicles it is observing
// to have this function simulate using math functions (i.e. y=y+1) and then populating
// that buffer would defeat the purpose of this real time exercise.
// There is a better I'm sure way but I want to finish this simple task this side of the New Year's 2018.
// Write the BufferB(0), BufferA(1)
		if( bab_switch == 0) {
			buffer_ab = Simulation.bufferb;		// read from BufferA, write to bufferB this time
		} else {
			buffer_ab = Simulation.buffera;		// read from BufferB, write to BufferA this time
		}
		int x = 0;
		int y = 0;
		for(int i = 0; i < rows; i++) {
			for( int j = 0; j < columns; j++) {
				count = 0;
				for( int o = 0; o < ObjectCount; o++) {
					x = planes[o][1];
					y = planes[o][2];
					if (( x == i) && (y == j)) {
						count += (1 << o);
					}
				}
				buffer_ab[i][j] = count + 48;
			}
		}
		return;
	}

	@Override
	public void run() {
		int flipbit = 0;
		while( Simulation.t.get() <=  10) {
			try {
				Simulation.SLockA.acquire();
				if ( flipbit == 0) {
					UpdateBufferAB( 0);
				} else {
					UpdateBufferAB( 1);
				}
				Simulation.SLockA.release();
				flipbit = flipbit ^ 1;

				Thread.sleep(100);

				Simulation.STimeLock.acquire();
				System.out.println( "UpdateAB: The current time is: " + Simulation.t.get() + "FlipBit: " + flipbit);
				Simulation.STimeLock.release();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
