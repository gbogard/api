open Types;

module Result = {
  let toOption =
    fun
    | Success(a) => Some(a)
    | _ => None;
};

module Page = {
  let extractId =
    fun
    | Page.SimplePage(p) => p.id
    | Page.CodePage(p) => p.id;

  let extractTitle =
    fun
    | Page.SimplePage(p) => p.title
    | Page.CodePage(p) => p.title;
};
