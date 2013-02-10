/*
 * git.h
 *
 *  Created on: 09/02/2013
 *      Author: marrony
 */

#ifndef GIT_H_
#define GIT_H_

#include "exec.h"

#include <sys/stat.h>

struct tree {
	FILE* fr;
	FILE* fw;
	char sha1[41];
};

void git_version(char version[100]);

void git_rev_parse(const char* refspec, char sha1[40]);
void git_update_ref(const char* refspec, const char sha1[40]);
void git_commit_tree(const char* message, const char tsha1[40], const char parent[40], char csha1[40]);
void git_hash_object(const void* bytes, int nbytes, char sha1[40]);

void git_add(const char* filename);
void git_write_tree(char sha1[40]);

void git_mktree_begin(struct tree* t);
void git_mktree_add(struct tree* t, int mode, const char* type, const char sha1[40], const char* file);
void git_mktree_end(struct tree* t);

#ifndef S_IFLNK
#define S_IFLNK 0120000
#endif

#ifndef S_ISLNK
#define S_ISLNK(mode) (((mode) & S_IFMT) == S_IFLNK)
#endif

#define S_IFGITLINK 0160000
#define S_ISGITLINK(mode) (((mode) & S_IFMT) == S_IFGITLINK)

static inline unsigned int stat_to_git_mode(mode_t mode)
{
	if(S_ISDIR(mode))
		return S_IFDIR;

	if(S_ISLNK(mode))
		return S_IFLNK;

	if(S_ISGITLINK(mode))
		return S_IFGITLINK;

	return S_IFREG | (((mode) & S_IEXEC) ? 0755 : 0644);
}

#define GIT_DIRECTORY              S_IFDIR
#define GIT_REGULAR_EXECUTABLE     (S_IFREG | 0755)
#define GIT_REGULAR_NON_EXECUTABLE (S_IFREG | 0644)
#define GIT_SYMBOLYC_LINK          S_IFLNK
#define GIT_LINK                   S_IFGITLINK

#define GIT_EMPTY_TREE "4b825dc642cb6eb9a060e54bf8d69288fbee4904"

#endif /* GIT_H_ */
