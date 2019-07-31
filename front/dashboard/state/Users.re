open Types;
open ReactUpdate;

type action =
  | SetCurrentUser(result(user));

module State = {
  type t = {currentUser: result(user)};

  let initial = {currentUser: NotAsked};

  let reducer = (action, _) =>
    switch (action) {
    | SetCurrentUser(user) => Update({currentUser: user})
    };
};
