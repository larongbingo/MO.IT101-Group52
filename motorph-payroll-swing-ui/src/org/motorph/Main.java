package org.motorph;

import com.google.inject.Guice;
import org.motorph.auth.LoginForm;
import org.motorph.auth.LoginViewModel;
import org.motorph.data.LoadData;
import org.motorph.employees.*;
import org.motorph.payroll.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        new App();
    }
}
