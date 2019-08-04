module DomPurify = {
  [@bs.module "dompurify"] external sanitize: string => string = "sanitize";
};

module Marked = {
  [@bs.module "marked"] external makeHtml: string => string = "default";
  let renderAndSanitize = content => DomPurify.sanitize(makeHtml(content));
};
