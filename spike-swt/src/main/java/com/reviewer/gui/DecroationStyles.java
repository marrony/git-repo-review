package com.reviewer.gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DecroationStyles {
  public static void main(String[] args) {
    Display display = new Display();
    Shell composite = new Shell(display);
    composite.setText("Decorations Example");

    composite.setLayout(new GridLayout(3, true));

    // The SWT.BORDER style
    Decorations d = new Decorations(composite, SWT.BORDER);
//    d = new Decorations(composite, SWT.CLOSE);
//    d = new Decorations(composite, SWT.MIN);
//    d = new Decorations(composite, SWT.MAX);
//    d = new Decorations(composite, SWT.NO_TRIM);
//    d = new Decorations(composite, SWT.RESIZE);
//    d = new Decorations(composite, SWT.TITLE);
//    d = new Decorations(composite, SWT.ON_TOP);
//   d = new Decorations(composite, SWT.TOOL);
    
    d.setLayoutData(new GridData(GridData.FILL_BOTH));
    d.setLayout(new FillLayout());
    new Label(d, SWT.CENTER).setText("SWT.BORDER");

    composite.open();
    while (!composite.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();

  }
}