open Types;
open Widget;

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

module Widget = {
  let extractId =
    fun
    | MarkdownText(w) => w.id
    | MultipleChoices(w) => w.id
    | InteractiveCode(w) => w.id;
};

module WidgetState = {
  let isRight =
    fun
    | Right(_) => true
    | _ => false;
};
