package com.reviewer.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Git {

	private static final int S_IFLNK = 0120000;
	private static final int S_IFGITLINK = 0160000;
	private static final int S_IFDIR = 0x4000;
	private static final int S_IFREG = 0x8000;
	private static final int S_IEXEC = 0x0040;
	private static final int S_IFMT = 0xF000;

	public static enum FileMode {
		DIRECTORY(S_IFDIR),
		REGULAR_EXECUTABLE(S_IFREG | 0755),
		REGULAR_NON_EXECUTABLE(S_IFREG | 0644),
		SYMBOLYC_LINK(S_IFLNK),
		LINK(S_IFGITLINK);

		private int flags;

		FileMode(int flags) {
			this.flags = flags;
		}
		
		public static FileMode fromFlags(int flags) {
			for(FileMode mode : values()) {
				if(flags == mode.flags)
					return mode;
			}
			return null;
		}
	}

	private static Process execGit(String format) throws IOException {
		return Runtime.getRuntime().exec(format);
	}

	private static Process execGitAndWait(String cmd) throws IOException {
		Process process = Runtime.getRuntime().exec(cmd);
		
		try {
			process.waitFor();
		} catch (InterruptedException e) {
		}
		
		return process;
	}
	
	private static BufferedReader execReadOnly(String cmd) throws IOException {
		return getReader(execGitAndWait(cmd));
	}
	
	private static BufferedReader getReader(Process process) {
		return new BufferedReader(new InputStreamReader(process.getInputStream()));
	}

	private static BufferedWriter getWriter(final Process process) {
		return new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
	}

	private static boolean S_ISDIR(int mode) {
		return (mode & S_IFMT) == S_IFDIR;
	}

	private static boolean S_ISLNK(int mode) {
		return (mode & S_IFMT) == S_IFLNK;
	}

	private static boolean S_ISGITLINK(int mode) {
		return (mode & S_IFMT) == S_IFGITLINK;
	}

	public static int stat_to_git_mode(int mode) {
		if (S_ISDIR(mode))
			return S_IFDIR;

		if (S_ISLNK(mode))
			return S_IFLNK;

		if (S_ISGITLINK(mode))
			return S_IFGITLINK;

		return S_IFREG | ((mode & S_IEXEC) != 0 ? 0755 : 0644);
	}

	public static String version() throws IOException {
		return execReadOnly("git --version").readLine();
	}

	public static String rev_parse(String refspec) throws IOException {
		return execReadOnly(String.format("git rev-parse %s", refspec)).readLine();
	}
	
	public static int[] rev_list_count_behind_and_ahead(String begin, String end) throws IOException {
		String line = execReadOnly(String.format("git rev-list --left-right --count %s...%s", begin, end)).readLine();
		
		int behind = 0;
		int ahead = 0;
		
		if(line != null) {
			String[] readLine = line.split("[\t]");
			behind = Integer.parseInt(readLine[0]);
			ahead = Integer.parseInt(readLine[1]);
		}
		
		return new int[] {behind, ahead};
	}
	
	public static List<String> rev_list(String begin, String end, boolean reverse) throws IOException {
		String rev = reverse ? "--reverse" : "";
		
		BufferedReader reader;
		
		if(begin != null && end != null)
			reader = execReadOnly(String.format("git rev-list %s %s %s", rev, begin, end));
		else
			reader = execReadOnly(String.format("git rev-list %s %s", rev, begin != null ? begin : end));
		
		List<String> commits = new ArrayList<String>();
		String line;
		while((line = reader.readLine()) != null)
			commits.add(line);
		
		return commits;
	}
	
	public static List<String> rev_list_range_reversed(String begin, String end) throws IOException {
		return rev_list(begin != null ? "^" + begin : null, end, true);
	}

	public static void update_ref(String refspec, String sha1) throws IOException {
		execGitAndWait(String.format("git update-ref %s %s", refspec, sha1));
	}

	public static String commit_tree(String message, String tsha1, String parent) throws IOException {
		final Process process;

		if (parent != null) {
			process = execGit(String.format("git commit-tree %s -p %s", tsha1, parent));
		} else {
			process = execGit(String.format("git commit-tree %s", tsha1));
		}

		BufferedWriter writer = getWriter(process);
		BufferedReader reader = getReader(process);

		writer.write(message);
		writer.close();

		return reader.readLine();
	}

	public static String calculate_hash(byte[] bytes) throws IOException {
		return hash_object(bytes, false);
	}
	
	public static String hash_object(byte[] bytes) throws IOException {
		return hash_object(bytes, true);
	}
	
	public static String hash_object(byte[] bytes, boolean write) throws IOException {
		Process process;
		
		if(write)
			process = execGit("git hash-object -w --stdin");
		else
			process = execGit("git hash-object --stdin");

		OutputStream stream = process.getOutputStream();
		BufferedReader reader = getReader(process);

		stream.write(bytes);
		stream.close();

		return reader.readLine();
	}

	public static void add(String filename) throws IOException {
		execGitAndWait(String.format("git add %s", filename));
	}

	public static String write_tree() throws IOException {
		BufferedReader reader = execReadOnly("git write-tree");
		return reader.readLine();
	}

	public static class Tree {
		private Process process;
		private BufferedWriter writer;
		private BufferedReader reader;

		public String sha1;

		public void add(FileMode mode, String type, String sha1, String file) throws IOException {
			writer.write(String.format("%06o %s %40s\t%s\n", mode.flags, type, sha1, file));
		}

		public void addBlob(FileMode mode, String sha1, String file) throws IOException {
			add(mode, "blob", sha1, file);
		}

		public void addTree(String sha1, String file) throws IOException {
			add(FileMode.DIRECTORY, "tree", sha1, file);
		}

		public void end() throws IOException {
			writer.close();

			sha1 = reader.readLine();

			reader.close();
		}
	}

	public static Tree mktree() throws IOException {
		Tree tree = new Tree();

		tree.process = execGit("git mktree");
		tree.writer = getWriter(tree.process);
		tree.reader = getReader(tree.process);

		return tree;
	}
	
	public static String file_type(String sha1) throws IOException {
		BufferedReader reader = execReadOnly(String.format("git cat-file -t %s", sha1));
		return reader.readLine();
	}
	
	public static class GitFile {
		public FileMode filemode;
		public String filetype;
		public String sha1;
		public String filename;
	}

	public static List<GitFile> ls_tree(String tree) throws IOException {
		BufferedReader reader;
		
		if("tree".equals(file_type(tree))) {
			reader = execReadOnly(String.format("git cat-file -p tree", tree));
		} else {
			reader = execReadOnly(String.format("git cat-file -p %s^{tree}", tree));
		}
		
		List<GitFile> files = new ArrayList<GitFile>();
		
		String line;
		while((line = reader.readLine()) != null) {
			String[] strings = line.split("[ \t\n]");
			
			String filemode = strings[0];
			String filetype = strings[1];
			String sha1 = strings[2];
			String filename = strings[3];
			
			GitFile file = new GitFile();

			file.filemode = FileMode.fromFlags(Integer.parseInt(filemode, 8));
			file.filetype = filetype;
			file.sha1 = sha1;
			file.filename = filename;
			
			files.add(file);
		}
		
		return files;
	}
	
	public static boolean delete_branch(String branch, boolean force) throws IOException {
		final String cmd;
		
		if(force)
			cmd = String.format("git branch -D %s", branch);
		else
			cmd = String.format("git branch -d %s", branch);
		
		return execGitAndWait(cmd).exitValue() == 0;
	}
	
	public static boolean delete_branch(String branch) throws IOException {
		return delete_branch(branch, false);
	}
	
	public static boolean delete_remote_branch(String remote, String branch) throws IOException {
		return push(remote, ":" + branch);
	}

	public static void cat_file(String sha1, OutputStream out) throws IOException {
		Process process = execGitAndWait(String.format("git cat-file -p %s", sha1));
		
		InputStream in = process.getInputStream();
		
		byte[] bytes = new byte[1024];
		while(in.available() > 0) {
			int nbytes = in.read(bytes);
			
			out.write(bytes, 0, nbytes);
		}
		
		in.close();
	}
	
	public static String author_name_and_date(String sha1) throws IOException {
		return execReadOnly(String.format("git log --format=%%an%%x09%%at -1 %s", sha1)).readLine();
	}
	
	public static String var(String var) throws IOException {
		return execReadOnly(String.format("git var %s", var)).readLine();
	}
	
	public static String config(String config) throws IOException {
		return execReadOnly(String.format("git config --get %s", config)).readLine();
	}
	
	public static String user_name() throws IOException {
		return config("user.name");
	}
	
	public static String user_email() throws IOException {
		return config("user.email");
	}
	
	public static boolean fetch(String remote, String branch) throws IOException {
		String cmd = "git fetch";
		
		if(remote != null)
			cmd += " " + remote;
		
		if(branch != null)
			cmd += " " + branch;
		
		return execGitAndWait(cmd).exitValue() == 0;
	}
	
	public static boolean push(String remote, String branch) throws IOException {
		String cmd = "git push";
		
		if(remote != null)
			cmd += " " + remote;
		
		if(branch != null)
			cmd += " " + branch;
		
		return execGitAndWait(cmd).exitValue() == 0;
	}
	
	public static String git_dir() throws IOException {
		return execReadOnly("git rev-parse --git-dir").readLine();
	}
	
	public static String merge_base(String a, String b) throws IOException {
		return execReadOnly(String.format("git merge-base --octopus %s %s", a, b)).readLine();
	}

	public static String get_tree(String commit) throws IOException {
		return execReadOnly(String.format("git show -s --format=%%T %s", commit)).readLine();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(Git.version());

		String sha1file = Git.hash_object("teste".getBytes());

		Tree tree = Git.mktree();
		tree.addBlob(FileMode.REGULAR_NON_EXECUTABLE, sha1file, "teste0.txt");
		tree.addBlob(FileMode.REGULAR_NON_EXECUTABLE, sha1file, "teste1.txt");
		tree.end();

		System.out.println(tree.sha1);

		System.out.println(Git.commit_tree("message", tree.sha1, null));

		System.out.println(Git.rev_parse("refs/heads/review"));
	}
}
