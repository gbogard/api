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
    ) =>
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

let fetchJson =
    (
      ~endpoint,
      ~baseUrl=Configuration.apiUrl,
      ~method=Fetch.Get,
      ~successDecoder: Serialization.decoder('a),
      ~errorDecoder: option(Serialization.decoder('b))=?,
      ~headers=?,
      ~body=?,
      (),
    ) => {
  let errorDecoder: Serialization.decoder('b) =
    errorDecoder |> Option.default(successDecoder);

  let decodeResponse = (decoder, response) =>
    Js.Promise.(
      Fetch.Response.json(response)
      |> then_(json => resolve(decoder(json)))
      |> then_(res => resolve(Success(res)))
    );

  Js.Promise.(
    fetch(~baseUrl, ~endpoint, ~method, ~headers?, ~body?, ())
    |> then_(
         fun
         | Success(response) => decodeResponse(successDecoder, response)
         | ClientError(response) => decodeResponse(errorDecoder, response)
         | _ => resolve(Failed),
       )
    |> catch(error => {
         Js.Console.error(error);
         resolve(Failed);
       })
  );
};
