module DomPurify = {
  [@bs.module "dompurify"] external sanitize: string => string = "sanitize";
};

module Marked = {
  [@bs.module "marked"] external makeHtml: string => string = "default";
  let renderAndSanitize = content => DomPurify.sanitize(makeHtml(content));
};

module Ace = {
  [@bs.deriving abstract]
  type options = {
    mode: string,
    theme: string,
  };

  [@bs.deriving abstract]
  type editor = {setOptions: options => unit};

  [@bs.deriving abstract]
  type ace = {edit: Dom.element => editor};

  [@bs.module "brace"] external aceEditor: ace = "default";

  let create = element => editGet(aceEditor, element);
};
