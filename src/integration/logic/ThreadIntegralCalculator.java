package integration.logic;

import integration.controller.Controller;
import java.util.function.DoubleUnaryOperator;

public class ThreadIntegralCalculator implements Runnable {
    private IntegralCalculator calculator;
    private Controller main;

    public ThreadIntegralCalculator(Controller main, double a, double b, int n, DoubleUnaryOperator f) {
        calculator = new IntegralCalculator(a,b,n,f);
        this.main = main;
    }

    @Override
    public void run() {
        double value = calculator.calculate();
        main.sendResult(value);
    }
}