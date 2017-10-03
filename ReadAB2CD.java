import java.lang.Thread;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

// This is the dangerous function, it uses two pairs of semaphores unlike the others which use just one
// So I can see deadlock being a problem, using "Praying to God" method of compilation here
public class ReadAB2CD implements Runnable
{
	private int rows;
	private int columns;
	private int ObjectCount;

	public ReadAB2CD( int r, int c, int oc) {
		rows = r;
		columns = c;
		ObjectCount = oc;
	}

	public void UpdateCDFromBufferAB( int bab_switch)
	{
		int buffer_ab[][];
		int buffer_cd[][];

		if (bab_switch == 0) {
			buffer_ab = Simulation.buffera;		// Read from BufferA
			buffer_cd = Simulation.bufferc;		// Use this to write BufferC
		} else {
			buffer_ab = Simulation.bufferb;		// Read from BufferB
			buffer_cd = Simulation.bufferd;		// Use this to write BufferD
		}
// Scan the buffer, an O(n^2) op, and populate BufferCD with a numeric value representing a bitwise
// storage of what vehicles are in a given block (this is an O(2^m) operation, so bad, very bad!)
// but for small values of m (three vehicles here) it stores nicely but has to do 8 comparions (ugly)
// bitwise and is probably a better way to extract count, it would eliminate the long if statement
		int count = 0;
		for( int i = 0; i < rows; i++) {
			for( int j = 0; j < columns; j++) {
				count = buffer_ab[i][j] - 48;
				if( count == 1 ) {
					buffer_cd[0][0] = 'x';
					buffer_cd[0][1] = i;
					buffer_cd[0][2] = j;
				} else if ( count == 2) {
					buffer_cd[1][0] = 'y';
					buffer_cd[1][1] = i;
					buffer_cd[1][2] = j;
				} else if ( count == 3) {
					buffer_cd[0][0] = 'x';
					buffer_cd[0][1] = i;
					buffer_cd[0][2] = j;

					buffer_cd[1][0] = 'y';
					buffer_cd[1][1] = i;
					buffer_cd[1][2] = j;
				} else if ( count == 4) {
					buffer_cd[2][0] = 'z';
					buffer_cd[2][1] = i;
					buffer_cd[2][2] = j;
				} else if ( count == 5) {
					buffer_cd[0][0] = 'x';
					buffer_cd[0][1] = i;
					buffer_cd[0][2] = j;

					buffer_cd[2][0] = 'z';
					buffer_cd[2][1] = i;
					buffer_cd[2][2] = j;
				} else if ( count == 6) {
					buffer_cd[1][0] = 'y';
					buffer_cd[1][1] = i;
					buffer_cd[1][2] = j;

					buffer_cd[2][0] = 'z';
					buffer_cd[2][1] = i;
					buffer_cd[2][2] = j;
				} else if ( count == 7) {
					buffer_cd[0][0] = 'x';
					buffer_cd[0][1] = i;
					buffer_cd[0][2] = j;

					buffer_cd[1][0] = 'y';
					buffer_cd[1][1] = i;
					buffer_cd[1][2] = j;

					buffer_cd[2][0] = 'z';
					buffer_cd[2][1] = i;
					buffer_cd[2][2] = j;
				} else {
					;
				}
			}
		}
	}

// Add small 100ms delay to allow UpdateAB to go first and this go before
// Display, a better way would be to use locks to allow one to go in order
// but we'll try this first...
// Update A -> C data (0) or B->D data (1)
	@Override
	public void run() {
		int flipbit = 0;
		while(Simulation.t.get() <= 10) {
			try {
				Simulation.SLockC.acquire();
				if( flipbit == 0) {
					UpdateCDFromBufferAB( flipbit);
				} else {
					UpdateCDFromBufferAB( flipbit);
				}
				Simulation.SLockC.release();

				Thread.sleep(100);

				Simulation.STimeLock.acquire();
					System.out.println( "ReadAB2CD: The current time is " + Simulation.t.get() + "FLIPBIT: " + flipbit);
				Simulation.STimeLock.release();

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

