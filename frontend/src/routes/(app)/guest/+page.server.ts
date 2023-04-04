export async function load({ params, url }) {
  let plantId = url.searchParams.get("plantId");
  let plantName: string = "Sakura";
  let roomName: string = "Room 1";
  // TODO: fetch proper backend endpoint
  return {
    plantName,
    roomName,
    pictures: "https:\/\/picsum.photos\/200\/300",
  };
}
