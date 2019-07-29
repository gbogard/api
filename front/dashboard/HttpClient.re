type result('a) =
  | NotAsked
  | Loading
  | Failure
  | Success('a);

let fetch =
    (
      ~baseUrl=Configuration.apiUrl,
      ~method=Fetch.Get,
      ~body=?,
      ~headers=?,
      ~endpoint,
    ) =>
  Js.Promise.(
    Fetch.fetchWithInit(
      baseUrl ++ "/" ++ endpoint,
      Fetch.RequestInit.make(~method_=method, ~headers?, ~body=?body)()
    )
    |> then_(response => resolve(Success(response)))
  );

let fetchJson =
    (
      ~baseUrl=Configuration.apiUrl,
      ~method=Fetch.Get,
      ~decoder: Serialization.decoder('a),
      ~headers=?,
      ~body=?,
      ~endpoint,
    ) =>
  Js.Promise.(
    fetch(~baseUrl, ~endpoint, ~method, ~headers?, ~body?)
    |> then_(result =>
         switch (result) {
         | Success(response) =>
           Fetch.Response.json(response)
           |> then_(json => resolve(decoder(json)))
           |> then_(res => resolve(Success(res)))
         | _ => resolve(Failure)
         }
       )
  );