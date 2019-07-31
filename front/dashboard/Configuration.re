[@bs.val] external showcaseUrl: string = "process.env.SHOWCASE_URL";
[@bs.val] external dashboardUrl: string = "process.env.DASHBOARD_URL";
[@bs.val] external apiUrl: string = "process.env.API_URL";

let extractBasePathRegex = [%bs.re "/https?:\/\/.*?\/(.*)/"];
let dashboardBasePath =
  extractBasePathRegex
  ->Js.Re.exec_(dashboardUrl)
  ->Belt.Option.flatMap(result =>
      Js.Re.captures(result)[1] |> Js.Nullable.toOption
    )
  ->Belt.Option.map(path => Js.String.split("/", path) |> Array.to_list)
  ->Belt.Option.getWithDefault([]);
