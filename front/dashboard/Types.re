type result('a) =
  | NotAsked
  | Loading
  | Failed
  | Success('a);

type user = {
  id: string,
  username: string,
};

module Page = {
  type simplePage = {id: string};

  type t =
    | SimplePage(simplePage);
};

type courseManifest = {
  id: string,
  title: string,
  description: string,
  tags: list(string),
};

type course = {
  id: string,
  title: string,
  description: string,
  tags: list(string),
  pages: list(Page.t),
};
