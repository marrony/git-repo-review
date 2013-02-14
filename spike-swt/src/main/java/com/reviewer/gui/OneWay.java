package com.reviewer.gui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class OneWay {
	public static void main(String[] args) {
		final Display display = new Display();
		final Shell shell = new Shell(display);
		final Map<Integer, Integer> lines = new HashMap<Integer, Integer>();
		Font font = new Font(display, "Consolas", 10, 0);

	    final FillLayout layout = new FillLayout(SWT.HORIZONTAL);
	    
		layout.marginHeight = 5;
	    layout.marginWidth = 5;
	    layout.spacing = 5;
	    
	    shell.setLayout(layout);
		
		final StyledText styledText = new StyledText(shell, SWT.V_SCROLL | SWT.BORDER);
		String text = 		
"diff --git a/spike-cpp/exec.c b/spike-cpp/exec.c\n" +
"index b502bd3..7c729b3 100644\n" +
"--- a/spike-cpp/exec.c\n" +
"+++ b/spike-cpp/exec.c\n" +
"@@ -23,11 +23,11 @@\n" +
" #define READ 0\n" +
" #define WRITE 1\n" +
"\n" +
"-#define PARENT_READ  read_pipe[READ]\n" +
"-#define CHILD_WRITE  read_pipe[WRITE]\n" +
"+#define PARENT_READ  pipe0[READ]\n" +
"+#define CHILD_WRITE  pipe0[WRITE]\n" +
"\n" +
"-#define CHILD_READ   write_pipe[READ]\n" +
"-#define PARENT_WRITE write_pipe[WRITE]\n" +
"+#define CHILD_READ   pipe1[READ]\n" +
"+#define PARENT_WRITE pipe1[WRITE]\n" +
"\n" +
" #ifndef pipe\n" +
" int pipe(int fd[2]) {\n" +
"@@ -36,11 +36,11 @@ int pipe(int fd[2]) {\n" +
" #endif\n" +
"\n" +
" int execute_program(const char* const* argv, FILE** fr, FILE** fw) {\n" +
"-       int read_pipe[2];\n" +
"-       int write_pipe[2];\n" +
"+       int pipe0[2];\n" +
"+       int pipe1[2];\n" +
"\n" +
"-       pipe(read_pipe);\n" +
"-       pipe(write_pipe);\n" +
"+       pipe(pipe0);\n" +
"+       pipe(pipe1);\n" +
"\n" +
"        int fdstdin = dup(STDIN_FILENO);\n" +
"        int fdstdout = dup(STDOUT_FILENO);";		
		
		styledText.setText(text);
		styledText.setFont(font);

		styledText.addLineBackgroundListener(new LineBackgroundListener() {
			public void lineGetBackground(LineBackgroundEvent event) {
				if(event.lineText.length() < 1)
					return;
				
				String substring = event.lineText.substring(0, 1);
				
				if (substring.startsWith("-")) {
//					int lineIndex = styledText.getLineAtOffset(event.lineOffset);
//					lines.add(lineIndex);
					
					lines.put(event.lineOffset, SWT.COLOR_RED);
					
					event.lineBackground = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				}
				
				if(substring.startsWith("+")) {
					lines.put(event.lineOffset, SWT.COLOR_GREEN);
					
					event.lineBackground = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
				}
			}
		});

//		styledText.addPaintListener(new PaintListener() {
//			public void paintControl(PaintEvent e) {
//				Rectangle bounds = styledText.getClientArea();
//				
//				for(Entry<Integer, Integer> entry : lines.entrySet()) {
//					Integer offset = entry.getKey();
//					Integer color = entry.getValue();
//					
//					Point point = styledText.getLocationAtOffset(offset);
//					int height = styledText.getLineHeight(offset);
//					
//					e.gc.drawRectangle(point.x, point.y, bounds.width-1, height);
//				}
//				
//				lines.clear();
//			}
//		});

		shell.setSize(500, 500);
		shell.open();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		font.dispose();
		display.dispose();
	}
}