open HttpClient;
open Serialization;
open Types;

let fetchCourses =
  fetchJson(~endpoint="/courses", ~decoder=Decode.courseManifests);

let fetchCourse = id =>
  fetchJson(~endpoint="/courses/" ++ id, ~decoder=Decode.course, ());

let checkWidget = (id, input: WidgetInput.t) =>
  fetchJson(
    ~endpoint="/checkWidget/" ++ id,
    ~decoder=Decode.WidgetOutput.decode,
    ~body=
      Fetch.BodyInit.make(
        input |> Encode.WidgetInput.encode |> Json.stringify,
      ),
    (),
  );
