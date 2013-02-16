package com.reviewer.git;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.eclipse.swt.internal.Platform;

public class Git {

	private static final int S_IFLNK = 0120000;
	private static final int S_IFGITLINK = 0160000;
	private static final int S_IFDIR = 0x4000;
	private static final int S_IFREG = 0x8000;
	private static final int S_IEXEC = 0x0040;
	private static final int S_IFMT = 0xF000;

	enum FileMode {
		DIRECTORY(S_IFDIR),
		REGULAR_EXECUTABLE(S_IFREG | 0755),
		REGULAR_NON_EXECUTABLE(S_IFREG | 0644),
		SYMBOLYC_LINK(S_IFLNK),
		LINK(S_IFGITLINK);

		private int flags;

		FileMode(int flags) {
			this.flags = flags;
		}
	}

	private static Process execGit(String format) throws IOException {
		return Runtime.getRuntime().exec(format);
	}

	private static BufferedReader getReader(Process process) {
		return new BufferedReader(new InputStreamReader(
				process.getInputStream()));
	}

	private static BufferedWriter getWriter(final Process process) {
		return new BufferedWriter(new OutputStreamWriter(
				process.getOutputStream()));
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
		Process process = execGit("git --version");
		BufferedReader reader = getReader(process);
		return reader.readLine();
	}

	public static String rev_parse(String refspec) throws IOException {
		Process process = execGit(String.format("git rev-parse %s", refspec));
		BufferedReader reader = getReader(process);
		return reader.readLine();
	}

	public static void update_ref(String refspec, String sha1)
			throws IOException, InterruptedException {
		Process process = execGit(String.format("git update-ref %s %s",
				refspec, sha1));
		process.waitFor();
	}

	public static String commit_tree(String message, String tsha1, String parent)
			throws IOException {
		final Process process;

		if (parent != null) {
			process = execGit(String.format("git commit-tree %s -p %s", tsha1,
					parent));
		} else {
			process = execGit(String.format("git commit-tree %s", tsha1));
		}

		BufferedWriter writer = getWriter(process);
		BufferedReader reader = getReader(process);

		writer.write(message);
		writer.close();

		String csha1 = reader.readLine();

		try {
			update_ref("refs/heads/review", csha1);
		} catch (InterruptedException e) {
			return null;
		}

		return csha1;
	}

	public static String hash_object(byte[] bytes) throws IOException {
		Process process = execGit("git hash-object -w --stdin");

		OutputStream stream = process.getOutputStream();
		BufferedReader reader = getReader(process);

		stream.write(bytes);
		stream.close();

		return reader.readLine();
	}

	public static void add(String filename) throws IOException {
		execGit(String.format("git add %s", filename));
	}

	public static String write_tree() throws IOException {
		Process process = execGit("git write-tree");
		BufferedReader reader = getReader(process);
		return reader.readLine();
	}

	public static class Tree {
		private Process process;
		private BufferedWriter writer;
		private BufferedReader reader;

		public String sha1;

		public void add(FileMode mode, String type, String sha1, String file)
				throws IOException {
			writer.write(String.format("%06o %s %40s\t%s\n", mode.flags, type,
					sha1, file));
		}

		public void addBlob(FileMode mode, String sha1, String file)
				throws IOException {
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
