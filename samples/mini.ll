@Two = global i32 2
define i32 @ll_main(i32 %x) {
entry:
  %six = alloca i32
  store i32 6, i32* %six
  %two = load i32* @Two
  %t1 = mul i32 %two, %x
  %sixval = load i32* %six
  %t2 = add i32 %t1, %sixval
  ret i32 %t2
}


