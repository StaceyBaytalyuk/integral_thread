package integration.controller;

import integration.logic.ThreadIntegralCalculator;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class Controller {
    @FXML
    private TextField intervalsTextField;
    @FXML
    private  TextField threadsTextField;
    @FXML
    private Button calculateButton;
    @FXML
    private  Text integralText;
    @FXML
    private  Text timeText;

    private double totalResult = 0;
    private int finishedThreadCount = 0;
    private final double a = 0;
    private final double b = Math.PI;
    //int n = 1000_000_000;
    private int n = 1_000_000;
    private int threads = 20;

    public void onOk() {
        boolean empty = (intervalsTextField.getText() == null || intervalsTextField.getText().isEmpty())
                || (threadsTextField.getText() == null || threadsTextField.getText().isEmpty());

        try {
            if (!empty) {
                n = Integer.parseInt(intervalsTextField.getText());
                threads = Integer.parseInt(threadsTextField.getText());
                if ( (n < 1) || (threads < 1) ) {
                    showAlert("Кількість потоків та інтервалів має бути доданою");
                } else {
                    calculateButton.setDisable(true);
                    calculate();
                    calculateButton.setDisable(false);
                }
            } else {
                showAlert("Будь ласка, введіть усі поля");
            }
        } catch (NumberFormatException e) {
            showAlert("Неправильний формат вводу");
        }

    }

    private void calculate() {
        integralText.setText("Розрахунок...");
        timeText.setText("");

        totalResult = 0;
        finishedThreadCount = 0;
        double delta = (b - a) / threads;
        long start = System.currentTimeMillis();
        for (int i = 0; i < threads; i++) {
            ThreadIntegralCalculator t = new ThreadIntegralCalculator(this, a + i * delta, a + (i + 1) * delta, n / threads, Math::sin);
            new Thread(t).start();
        }

        try {
            synchronized (this) {
                while (finishedThreadCount < threads) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long finish = System.currentTimeMillis();

        integralText.setText("Результат: "+totalResult);
        timeText.setText("Час виконання: "+(finish-start)+" мілісекунд");
    }

    public synchronized void sendResult(double v) {
        totalResult += v;
        finishedThreadCount++;
        notify();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Помилка");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}