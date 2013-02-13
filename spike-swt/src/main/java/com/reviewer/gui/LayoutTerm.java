package com.reviewer.gui;
import javax.swing.text.AbstractDocument.BranchElement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;

public class LayoutTerm {
  public static void main(String[] args) {
    Display display = new Display();
    Shell shell = new Shell(display);
    Decorations decorations = new Decorations(shell, SWT.NONE);

    System.out.println("Bounds: " + shell.getBounds());
    System.out.println("Client area: " + shell.getClientArea());

    shell.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

    FillLayout vLayout = new FillLayout(SWT.VERTICAL);
    FillLayout hLayout = new FillLayout(SWT.VERTICAL); //new RowLayout(SWT.HORIZONTAL);
    
    vLayout.marginHeight = 5;
    vLayout.marginWidth = 5;
    vLayout.spacing = 10;
    
    hLayout.marginHeight = 5;
    hLayout.marginWidth = 5;
    hLayout.spacing = 10;

    shell.setLayout(vLayout);
    decorations.setLayout(hLayout);
    
    Browser browser = new Browser(shell, SWT.NONE);
    browser.setText("<HTML><HEAD><TITLE>HTML Test</TITLE></HEAD><BODY>" +
    "<a href=\"http://www.java2s.com\">java2s.com</a><br><br>" +
    "</BODY></HTML>");
    //browser.setUrl("http://www.google.com");

    Button button1 = new Button(shell, SWT.PUSH);
    button1.setText("button1");

    Button button2 = new Button(shell, SWT.PUSH);
    button2.setText("button2");

    Button button3 = new Button(decorations, SWT.PUSH);
    button3.setText("button3");
    
    Button button4 = new Button(decorations, SWT.PUSH);
    button4.setText("button4");

    shell.pack();
    shell.open();

    System.out.println("button1: " + button1.getBounds());
    System.out.println("button2: " + button2.getBounds());

    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}