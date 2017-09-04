package com.jeff;

import java.io.IOException;

public class ProcessC extends ProcessBase {

    private final DoubleBuffer<Object[][]> _bufferCD;
    private Plane _statusPlane;
    private Display _display;
    private int _second = 0;
    private int _collisions = 0;

    public ProcessC(DoubleBuffer<Object[][]> bufferCD,
                    Display display,
                    Console console) {
        super(console);
        _bufferCD = bufferCD;
        _display = display;
        _statusPlane = display.GetStatusPlane();
    }

    @Override
    public void run() {

        Object[][] state;

        while (!_bufferCD.isShutdown() && _second <= _display.Seconds) {
            state = _bufferCD.pull();
            System.out.println("C pulled: " + state.length);
            ConsoleWriteLine("Second " + _second);
            OutputState(state);
            ShowState(state);
            _second++;
        }
        String finMsg = "DONE. " + _collisions + " collisions occurred over " + _display.Seconds + " seconds.";
        _display.UpdateStatus(finMsg);
        ConsoleWriteLine(finMsg);
        System.out.println("BufferCD completed");
        ConsoleWait();
    }

    private void OutputState(Object[][] state) {
        for (int plane = 0; plane < state.length; plane++) {
            ConsoleWriteLine(state[plane][0] + " | " + state[plane][1] + " | " + state[plane][2]);
        }
        ConsoleWriteLine();
        try {
            _display.Refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ShowState(Object[][] state) {
        _statusPlane.ResetDisplay();
        _display.UpdateStatus("Process C Second " + _second);
        for (int plane = 0; plane < state.length; plane++) {
            String marker = (String)state[plane][0];
            int row = (int)state[plane][1];
            int col = (int)state[plane][2];
            String currentMarker = _statusPlane.GetMarker(row, col) + marker;
            boolean collision = currentMarker.length() > 1;
            _statusPlane.ShowMarker(row, col, collision, currentMarker);
            if (collision) {
                _display.UpdateStatus(", COLLISION " + currentMarker, true);
                ConsoleWriteLine("*** COLLISION " + currentMarker + " at second " + _second + "\r\n");
                _collisions++;
            }
        }
        try {
            _display.Refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
