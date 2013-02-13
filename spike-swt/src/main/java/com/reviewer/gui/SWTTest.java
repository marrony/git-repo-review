package com.reviewer.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SWTTest {

	public static void main(String[] args) {
		final Display display = new Display();
		Shell shell = new Shell(display);
		
//		Button button = new Button(shell, SWT.PUSH); 
//		button.setText("button");
//		button.pack();
		
		StyledText text = new StyledText(shell, SWT.NONE);
		text.setText("This is the text in the label");
//		text.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
//		text.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		text.setStyleRange(new StyleRange(1, 10, new Color(display, 0, 0, 255), null, SWT.NORMAL));
		text.setBounds(0, 0, 200, 200);
		//text.pack();
		
		text.addPaintListener(new PaintListener() {
			
			public void paintControl(PaintEvent event) {
				//event.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
//				event.gc.setFillRule(arg0)
				//event.gc.setAlpha(127);
				event.gc.drawRectangle(0, 0, 50, 12);
				//event.gc.drawText(arg0, arg1, arg2)
			}
		});
		
		
		Canvas canvas = new Canvas(shell, SWT.NONE);
		canvas.addPaintListener(new PaintListener() {
			
			public void paintControl(PaintEvent event) {
				//event.gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
				//event.gc.fillRectangle(0, 0, 50, 50);
				//event.gc.drawText(arg0, arg1, arg2)
			}
		});
		
		canvas.setBounds(0, 0, 50, 50);
		//canvas.pack();
		
		shell.open();
		// Create and check the event loop
		while (!shell.isDisposed()) {
		  if (!display.readAndDispatch())
		    display.sleep();
		}
		display.dispose();		
	}
	
}
