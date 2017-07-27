export FOO=(
  "a"
  "b"
)
bar ${FOO[*]}
bar ${FOO[123]}
bar ${FOO[123+123]}
