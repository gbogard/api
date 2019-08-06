open Types;
open Rationale;

let fetch =
    (
      ~endpoint,
      ~baseUrl=Configuration.apiUrl,
      ~method=Fetch.Get,
      ~body=?,
      ~headers=?,
      (),
    )
    : Js.Promise.t(result(Fetch.Response.t, Fetch.Response.t)) =>
  Js.Promise.(
    Fetch.fetchWithInit(
      baseUrl ++ endpoint,
      Fetch.RequestInit.make(~method_=method, ~headers?, ~body?, ()),
    )
    |> then_(response =>
         resolve(
           switch (Fetch.Response.status(response)) {
           | status when status >= 500 => Failed
           | status when status >= 400 => ClientError(response)
           | _ => Success(response)
           },
         )
       )
    |> catch(e => {
         Js.Console.error(e);
         resolve(Failed);
       })
  );

let responseToJson =
    (
      successDecoder: Serialization.decoder('a),
      errorDecoder: Serialization.decoder('b),
      response: result(Fetch.Response.t, Fetch.Response.t),
    ) =>
  Js.Promise.(
    switch (response) {
    | Success(res) =>
      res
      |> Fetch.Response.json
      |> then_(json => resolve(Success(successDecoder(json))))
    | ClientError(res) =>
      res
      |> Fetch.Response.json
      |> then_(json => resolve(ClientError(errorDecoder(json))))
    | Failed => resolve(Failed)
    | NotAsked => resolve(Failed)
    | Loading => resolve(Loading)
    }
  );
