open HttpClient;
open Serialization;
open Types;

let fetchCourses =
  fetchJson(~endpoint="/courses", ~successDecoder=Decode.courseManifests);

let fetchCourse = id =>
  fetchJson(~endpoint="/courses/" ++ id, ~successDecoder=Decode.course, ());

let checkWidget = (id, input: WidgetInput.t): Js.Promise.t(widgetState) =>
  Js.Promise.(
    fetchJson(
      ~endpoint="/checkWidget/" ++ id,
      ~successDecoder=Decode.WidgetOutput.decode,
      ~errorDecoder=Decode.WidgetError.decode,
      ~body=
        Fetch.BodyInit.make(
          input |> Encode.WidgetInput.encode |> Json.stringify,
        ),
      (),
    )
    |> then_(
         fun
         | Success(widgetOutput) => Right(widgetOutput)
         | ClientError(widgetError) => Wrong(widgetError)
         | _ => Initial,
       )
  );
