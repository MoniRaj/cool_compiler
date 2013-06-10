#include <stdio.h>

/*
 * IO Class
 */

//obj for IO
struct obj_IO {
    struct class_IO* class_ptr; 
};

//IO.abort
void IO_abort(struct obj_IO* this, char* message) {
    fprintf(stderr, "%s", message);
    exit(1);
}

//IO.out
struct obj_IO* method_out(struct obj_IO* this, char* message) {
    printf("%s", message);
    return this;
}

char* IO_in(struct obj_IO* this) {
    int numchar = 0;
    char c;
    char* in;
    char tmp[1000];
    while ((c = getchar()) != '\n') {
        tmp[numchar] = c;
        numchar++;
        if (numchar >= 1000) {
            fprintf(stderr, "Error: input cannot exceed 1000 chars");
            exit(1);
        }
    }
    tmp[numchar] = '\0';
    in = tmp;
    //fprintf(stdout, "%s", tmp);
    return in;
}

int main(int junk) {
        return 0;
}
