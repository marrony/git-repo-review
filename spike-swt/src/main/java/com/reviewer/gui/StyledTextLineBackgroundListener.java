package com.reviewer.gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledTextLineBackgroundListener {
  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);

    final StyledText styledText = new StyledText(shell, SWT.V_SCROLL | SWT.BORDER);
    styledText.setText("\n1234\n124\n\1234\n12314\n\1241234\n");

    styledText.addLineBackgroundListener(new LineBackgroundListener() {

      public void lineGetBackground(LineBackgroundEvent event) {
        if (event.lineText.indexOf("SWT") > -1) {
          event.lineBackground = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
        }

      }
    });

    styledText.setBounds(10, 10, 500, 100);
    shell.pack();
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}