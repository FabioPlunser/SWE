export async function load({ params, url }) {
  //let userId = url.searchParams.get("userId");

  // TODO: fetch proper backend endpoint
  let username: string = "Sakura";
  let userEmail: string = "sakura.tree@mail.com";
  let userPassword: string = "1stPasswdOfSakura";
  let userPermissions: { [role: string]: boolean } = {
    user: true,
    gardener: true,
    admin: false,
  };

  return {
    username,
    userEmail,
    userPassword,
    userPermissions,
  };
}
