import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScrumTimer {
    private JSpinner comboSpeakers;
    private JLabel lblSpeakers;
    private JButton btnStart;
    private JButton btnStop;
    private JPanel rootPanel;
    private JPanel panelTimerControl;
    private JPanel panelInputs;
    private JProgressBar barSingle;
    private JProgressBar barTotal;
    private JPanel panelTimers;
    private final int SECONDS_PER_SPEAKER = 120;
    private int singleSecondCounter;
    private int currentSpeaker = 1;
    private int totalSecondCounter;
    private enum PossibleTimerStatus {STARTED, PAUSED, STOPPED};
    PossibleTimerStatus timerStatus;
    ScheduledExecutorService executor;

    public static void main(String[] args) {
        JFrame frame = new JFrame("SM Timer");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
        frame.setDefaultLookAndFeelDecorated(true);
        frame.setContentPane(new ScrumTimer().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.pack();
        frame.setVisible(true);
    }

    public ScrumTimer() {
        timerStatus = PossibleTimerStatus.STOPPED;
        barSingle.setString(secondsToMinutes(SECONDS_PER_SPEAKER));
        barSingle.setMaximum(SECONDS_PER_SPEAKER);
        barSingle.setValue(0);
        barTotal.setValue(0);
        barTotal.setMaximum(SECONDS_PER_SPEAKER);
        barTotal.setString(secondsToMinutes(((int) comboSpeakers.getValue()) * SECONDS_PER_SPEAKER));
        comboSpeakers.setValue(1);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
                barTotal.setString(secondsToMinutes(((int) comboSpeakers.getValue()) * SECONDS_PER_SPEAKER));
                barTotal.setMaximum(SECONDS_PER_SPEAKER * (int) comboSpeakers.getValue());
                timerStatus = PossibleTimerStatus.STARTED;

                executor = Executors.newScheduledThreadPool(1);
                executor.scheduleAtFixedRate(updateSingleBar, 0, 1, TimeUnit.SECONDS);

            }
        });
        btnStop.addActionListener(new ActionListener() {
            /**
             * Invoked when button is clicked.
             *
             * @param e the event to be processed
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                executor.shutdown();
                singleSecondCounter=0;
                totalSecondCounter=0;
                btnStop.setEnabled(false);
                btnStart.setEnabled(true);
                resetProgress();
                timerStatus = PossibleTimerStatus.STOPPED;
            }
        });
    }
    Runnable updateSingleBar = new Runnable() {
        public void run() {
            singleSecondCounter++;
            totalSecondCounter++;
            if (singleSecondCounter > SECONDS_PER_SPEAKER) {
                singleSecondCounter = 0;
                currentSpeaker++;
            }
            barSingle.setString(secondsToMinutes(SECONDS_PER_SPEAKER - singleSecondCounter ) + " | " + currentSpeaker + "/" + comboSpeakers.getValue());
            barSingle.setValue(singleSecondCounter);

            barTotal.setString(secondsToMinutes((((int) comboSpeakers.getValue()) * SECONDS_PER_SPEAKER) - totalSecondCounter));
            barTotal.setValue(totalSecondCounter);
        }
    };
    private void resetProgress() {
        barSingle.setValue(0);
        barSingle.setString(secondsToMinutes(SECONDS_PER_SPEAKER));
        barTotal.setValue(0);
        barTotal.setString(secondsToMinutes(((int) comboSpeakers.getValue()) * SECONDS_PER_SPEAKER));
    }

    private String secondsToMinutes(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return minutes + "m " + seconds + "s";
    }
}
