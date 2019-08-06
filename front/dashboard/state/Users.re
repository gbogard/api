open Types;
open ReactUpdate;

type action =
  | SetCurrentUser(result(user, nothing));

module State = {
  type t = {currentUser: result(user, nothing)};

  let initial = {currentUser: NotAsked};

  let reducer = (action, _) =>
    switch (action) {
    | SetCurrentUser(user) => Update({currentUser: user})
    };
};
