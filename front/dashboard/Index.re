let app =
  <Store.Provider>
    <> <BsReactHelmet defaultTitle="Lambdacademy Dashboard" /> <Router /> </>
  </Store.Provider>;

ReactDOMRe.renderToElementWithId(app, "app");