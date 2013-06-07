	.section	__TEXT,__text,regular,pure_instructions
	.globl	_Single_constructor
	.align	4, 0x90
_Single_constructor:                    ## @Single_constructor
	.cfi_startproc
## BB#0:
	pushq	%rax
Ltmp1:
	.cfi_def_cfa_offset 16
	movl	$8, %edi
	callq	_malloc
	leaq	_Single(%rip), %rcx
	movq	%rcx, (%rax)
	movl	$0, 8(%rax)
	popq	%rdx
	ret
	.cfi_endproc

	.globl	_Single_incx
	.align	4, 0x90
_Single_incx:                           ## @Single_incx
	.cfi_startproc
## BB#0:
	addl	%esi, 8(%rdi)
	ret
	.cfi_endproc

	.globl	_Single_getval
	.align	4, 0x90
_Single_getval:                         ## @Single_getval
	.cfi_startproc
## BB#0:
	movl	8(%rdi), %eax
	ret
	.cfi_endproc

	.globl	_Pair_constructor
	.align	4, 0x90
_Pair_constructor:                      ## @Pair_constructor
	.cfi_startproc
## BB#0:
	pushq	%rax
Ltmp3:
	.cfi_def_cfa_offset 16
	movl	$12, %edi
	callq	_malloc
	leaq	_Pair(%rip), %rcx
	movq	%rcx, (%rax)
	movq	$0, 8(%rax)
	popq	%rdx
	ret
	.cfi_endproc

	.globl	_Pair_incy
	.align	4, 0x90
_Pair_incy:                             ## @Pair_incy
	.cfi_startproc
## BB#0:
	addl	%esi, 12(%rdi)
	ret
	.cfi_endproc

	.globl	_Pair_getval
	.align	4, 0x90
_Pair_getval:                           ## @Pair_getval
	.cfi_startproc
## BB#0:
	movl	8(%rdi), %eax
	addl	12(%rdi), %eax
	ret
	.cfi_endproc

	.globl	_ll_main
	.align	4, 0x90
_ll_main:                               ## @ll_main
	.cfi_startproc
## BB#0:
	pushq	%rbp
Ltmp9:
	.cfi_def_cfa_offset 16
	pushq	%r15
Ltmp10:
	.cfi_def_cfa_offset 24
	pushq	%r14
Ltmp11:
	.cfi_def_cfa_offset 32
	pushq	%rbx
Ltmp12:
	.cfi_def_cfa_offset 40
	pushq	%rax
Ltmp13:
	.cfi_def_cfa_offset 48
Ltmp14:
	.cfi_offset %rbx, -40
Ltmp15:
	.cfi_offset %r14, -32
Ltmp16:
	.cfi_offset %r15, -24
Ltmp17:
	.cfi_offset %rbp, -16
	movl	%edi, %r14d
	callq	*_Single+8(%rip)
	movq	%rax, %rbx
	movq	(%rbx), %r15
	movq	16(%r15), %rbp
	movq	%rbx, %rdi
	movl	%r14d, %esi
	callq	*%rbp
	movq	%rbx, %rdi
	movl	%r14d, %esi
	callq	*%rbp
	movq	%rbx, %rdi
	callq	*24(%r15)
	callq	*_Pair+8(%rip)
	movq	%rax, %rbx
	movq	(%rbx), %rbp
	movq	%rbx, %rdi
	movl	%r14d, %esi
	callq	*16(%rbp)
	movq	%rbx, %rdi
	movl	$13, %esi
	callq	*32(%rbp)
	movq	%rbx, %rdi
	callq	*24(%rbp)
	addq	$8, %rsp
	popq	%rbx
	popq	%r14
	popq	%r15
	popq	%rbp
	ret
	.cfi_endproc

	.section	__DATA,__data
	.globl	_Single                 ## @Single
	.align	4
_Single:
	.quad	0
	.quad	_Single_constructor
	.quad	_Single_incx
	.quad	_Single_getval

	.globl	_Pair                   ## @Pair
	.align	4
_Pair:
	.quad	_Single
	.quad	_Pair_constructor
	.quad	_Pair_incx
	.quad	_Pair_getval
	.quad	_Pair_incy


	.globl	_Pair_incx
_Pair_incx = _Single_incx
.subsections_via_symbols
