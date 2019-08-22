module DomPurify = {
  [@bs.module "dompurify"] external sanitize: string => string = "sanitize";
};

module Marked = {
  [@bs.module "marked"] external makeHtml: string => string = "default";
  let renderAndSanitize = content => DomPurify.sanitize(makeHtml(content));
};

module Ace = {
  [@bs.module] external scalaMode: string = "brace/mode/scala";
  [@bs.module] external chromeTheme: string = "brace/theme/chrome";
  [scalaMode, chromeTheme] |> ignore;

  [@bs.deriving abstract]
  type options = {
    mode: string,
    [@bs.optional]
    theme: string,
    [@bs.optional]
    fontSize: string,
  };

  [@bs.deriving abstract]
  type editor = {
    .
    [@bs.meth] "setOptions": options => unit,
    [@bs.meth] "setValue": (string, int) => unit,
    [@bs.meth] "getValue": unit => string,
    [@bs.meth] "on": (string, unit => unit) => unit,
    [@bs.meth] "resize": unit => unit,
  };

  [@bs.deriving abstract]
  type ace = {. [@bs.meth] "edit": Dom.element => editor};

  [@bs.module "brace"] external aceEditor: ace = "default";

  let create = (element, options, value) => {
    let editor = aceEditor##edit(element);
    editor##setOptions(options);
    editor##setValue(value, 1);
    editor;
  };
};

module Drawer = {
  [@bs.module "react-motion-drawer"] [@react.component]
  external make:
    (~_open: bool, ~onChange: bool => unit, ~children: React.element) =>
    React.element =
    "default";
};
