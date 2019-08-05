open Types;
open ReactUpdate;

type action('nothing) =
  | SetCurrentUser(result(user, 'nothing));

module State = {
  type t('nothing) = {currentUser: result(user, 'nothing)};

  let initial = {currentUser: NotAsked};

  let reducer = (action, _) =>
    switch (action) {
    | SetCurrentUser(user) => Update({currentUser: user})
    };
};
