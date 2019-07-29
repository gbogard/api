open Types;

type decoder('a) = Js.Json.t => 'a;

module Decode = {
  let courseManifest = (json) =>
    Json.Decode.{
      id: json |> field("id", string),
      title: json |> field("title", string),
      description: json |> field("description", string),
      tags: json |> field("tags", list(string)),
    };
  let courseManifests = Json.Decode.list(courseManifest)
};