package com.reviewer.gui;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class JavascriptExec {
  public static void main(String[] args) {
    final String html = "<html><title>Snippet</title><body><p id='myid'>Best Friends</p><p id='myid2'>Cat and Dog</p></body></html>";
    Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setLayout(new FillLayout());
    final Browser browser = new Browser(shell, SWT.BORDER);
    Composite comp = new Composite(shell, SWT.NONE);
    comp.setLayout(new FillLayout(SWT.VERTICAL));
    final Text text = new Text(comp, SWT.MULTI);
    text.setText("var newNode = document.createElement('P'); \r\n"
        + "var text = document.createTextNode('At least when I am around');\r\n"
        + "newNode.appendChild(text);\r\n"
        + "document.getElementById('myid').appendChild(newNode);\r\n" + "\r\n"
        + "document.bgColor='yellow';");
    final Button button = new Button(comp, SWT.PUSH);
    button.setText("Execute Script");
    button.addListener(SWT.Selection, new Listener() {
      public void handleEvent(Event event) {
        boolean result = browser.execute(text.getText());
        if (!result) {
          /* Script may fail or may not be supported on certain platforms. */
          System.out.println("Script was not executed.");
        }
      }
    });
    browser.setText(html);
    shell.open();
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch())
        display.sleep();
    }
    display.dispose();
  }
}