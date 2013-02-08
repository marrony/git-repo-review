/*
 * main.cpp
 *
 *  Created on: 07/02/2013
 *      Author: marrony
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <io.h>
#include <fcntl.h>
#include <process.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>

#define READ 0
#define WRITE 1

#define	PARENT_READ  read_pipe[READ]
#define	CHILD_WRITE  read_pipe[WRITE]

#define CHILD_READ   write_pipe[READ]
#define PARENT_WRITE write_pipe[WRITE]

int execute_program(const char* const* argv, FILE** fr, FILE** fw) {
	int read_pipe[2];
	int write_pipe[2];

	_pipe(read_pipe, 256, O_BINARY | O_NOINHERIT);
	_pipe(write_pipe, 256, O_BINARY | O_NOINHERIT);

	int fdstdin = _dup(_fileno(stdin));
	int fdstdout = _dup(_fileno(stdout));

	if(_dup2(CHILD_READ, _fileno(stdin)) != 0) {
		printf("dup2\n");
		return 1;
	}

	if(_dup2(CHILD_WRITE, _fileno(stdout)) != 0) {
		printf("dup2\n");
		return 1;
	}

	close(CHILD_READ);
	close(CHILD_WRITE);

	int id = _spawnvp(P_NOWAIT, argv[0], argv);
	if(id < 0)
		return 1;

	if(_dup2(fdstdin, _fileno(stdin)) != 0) {
		printf("dup2\n");
		return 1;
	}

	if(_dup2(fdstdout, _fileno(stdout)) != 0) {
		printf("dup2\n");
		return 1;
	}

	close(fdstdin);
	close(fdstdout);

	*fr = fdopen(PARENT_READ, "r");
	*fw = fdopen(PARENT_READ, "w");

	return 0;
}

int main(int argc, char* argv[]) {
	char bytes[256+1];
	int nbytes;
	FILE* fr;
	FILE* fw;
	const char* args[] = {"git", "--version", NULL};

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

	fclose(fr);
	fclose(fw);

	return 0;
}
