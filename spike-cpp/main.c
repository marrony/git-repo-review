/*
 * main.cpp
 *
 *  Created on: 07/02/2013
 *      Author: marrony
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <errno.h>

#ifdef WIN32
#include <io.h>
#include <process.h>
#endif

#define READ 0
#define WRITE 1

#define PARENT_READ  read_pipe[READ]
#define CHILD_WRITE  read_pipe[WRITE]

#define CHILD_READ   write_pipe[READ]
#define PARENT_WRITE write_pipe[WRITE]

int execute_program(char** argv, FILE** fr, FILE** fw) {
	int read_pipe[2];
	int write_pipe[2];

	pipe(read_pipe);
	pipe(write_pipe);

	int fdstdin = dup(STDIN_FILENO);
	int fdstdout = dup(STDOUT_FILENO);

	if(dup2(CHILD_READ, STDIN_FILENO) < 0)
		return 1;

	if(dup2(CHILD_WRITE, STDOUT_FILENO) < 0)
		return 1;

	close(CHILD_READ);
	close(CHILD_WRITE);

#ifdef WIN32
	int id = _spawnvp(P_NOWAIT, argv[0], argv);
#else
	int id = fork();

	if(id == 0) {
		execvp(argv[0], argv);
		exit(0);
	}
#endif

	if(id < 0)
		return 1;

	if(dup2(fdstdin, STDIN_FILENO) < 0)
		return 1;

	if(dup2(fdstdout, STDOUT_FILENO) < 0)
		return 1;

	close(fdstdin);
	close(fdstdout);

	*fr = fdopen(PARENT_READ, "r");
	*fw = fdopen(PARENT_WRITE, "w");

	return 0;
}

int main(int argc, char* argv[]) {
	char bytes[256+1];
	int nbytes;
	FILE* fr = NULL;
	FILE* fw = NULL;
	char* args[] = {"git", "--version", NULL};

	if(execute_program(args, &fr, &fw))
		return 1;

	while(!feof(fr)) {
		nbytes = fread(bytes, 1, 256, fr);

		if(nbytes == 0)
			break;

		if(nbytes < 0 && errno == EINTR) {
			continue;
		}

		if(nbytes < 0)
			break;

		bytes[nbytes] = 0;

		printf("%s", bytes);
	}

	if(fr) fclose(fr);
	if(fw) fclose(fw);

	return 0;
}
