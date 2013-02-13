package com.reviewer.gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SyntaxColoring {
  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);

    final StyledText styledText = new StyledText(shell, SWT.V_SCROLL | SWT.BORDER);

    final String PUNCTUATION = "(){}";
    styledText.addExtendedModifyListener(new ExtendedModifyListener() {
      public void modifyText(ExtendedModifyEvent event) {
        int end = event.start + event.length - 1;

        if (event.start <= end) {
          String text = styledText.getText(event.start, end);
          java.util.List ranges = new java.util.ArrayList();

          for (int i = 0, n = text.length(); i < n; i++) {
            if (PUNCTUATION.indexOf(text.charAt(i)) > -1) {
              ranges.add(new StyleRange(event.start + i, 1, display.getSystemColor(SWT.COLOR_BLUE),
                  null, SWT.BOLD));
            }
          }
          if (!ranges.isEmpty()) {
            styledText.replaceStyleRanges(event.start, event.length, (StyleRange[]) ranges
                .toArray(new StyleRange[0]));
          }
        }
      }
    });

    styledText.setBounds(10, 10, 500, 100);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
    display.dispose();
  }
}