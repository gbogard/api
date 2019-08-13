module InlineSpinner = {
  [@react.component]
  let make = (~white=false) =>
    <div className={"inline-spinner" ++ (white ? " white" : "")}>
      <div className="bounce1" />
      <div className="bounce2" />
      <div className="bounce3" />
    </div>;
};
