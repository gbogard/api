open HttpClient;
open Serialization;
open Types;

let fetchCourses = (): Js.Promise.t(result(list(courseManifest), nothing)) =>
  fetch(~endpoint="/courses", ())
  |> Js.Promise.then_(responseToJson(Decode.courseManifests, Decode.nothing));

let fetchCourse = id =>
  fetch(~endpoint="/courses/" ++ id, ())
  |> Js.Promise.then_(responseToJson(Decode.course, Decode.nothing));

let checkWidget = (id, input: WidgetInput.t): Js.Promise.t(widgetState) =>
  Js.Promise.(
    fetch(
      ~method=Fetch.Post,
      ~endpoint="/checkWidget/" ++ id,
      ~body=
        Fetch.BodyInit.make(
          input |> Encode.WidgetInput.encode |> Json.stringify,
        ),
      (),
    )
    |> then_(
         responseToJson(
           Decode.WidgetOutput.decode,
           Decode.WidgetError.decode,
         ),
       )
    |> then_(
         fun
         | Success(widgetOutput) => resolve(Right(widgetOutput))
         | ClientError(widgetError) => resolve(Wrong(widgetError))
         | _ => resolve(Initial),
       )
  );
