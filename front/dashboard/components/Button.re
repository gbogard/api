type variants =
  | Primary
  | Info;

[@react.component]
let make =
    (
      ~children,
      ~variant=Primary,
      ~disabled=false,
      ~onClick=_ => (),
      ~isLoading=false,
    ) => {
  let variantClass =
    switch (variant) {
    | Primary => "button-primary"
    | Info => "button-info"
    };

  let clickHandler = isLoading ? _ => () : onClick;

  <button disabled onClick=clickHandler className={"button " ++ variantClass}>
    {isLoading ? <Spinner.InlineSpinner white=true /> : children}
  </button>;
};
