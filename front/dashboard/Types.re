type result('a) =
  | NotAsked
  | Loading
  | Failed
  | Success('a);

type courseManifest = {
  id: string,
  title: string,
  description: string,
  tags: list(string)
}

type user = {
  id: string,
  username: string,
}