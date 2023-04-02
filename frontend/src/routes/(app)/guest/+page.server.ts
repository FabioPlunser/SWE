export async function load({ params, url }) {
  let plantId = url.searchParams.get("plantId");
  let plantName = "Sakura";
  let roomName = "Room 1";
  let pictures = [
    "https://www.cnet.com/a/img/resize/191c48953f219022a1e2caf72a879ede437acdc7/hub/2021/12/06/db21c939-8275-4833-a656-6b044869a11d/warframe-the-new-war-teshin-screenshot-2.jpg?auto=webp&fit=crop&height=675&width=1200",
    "https://www3.nhk.or.jp/nhkworld/fr/news/backstories/2331/images/geM9EJcCyAImlHFIjsH5cmlkkOq8cFDeAWl77CWu.jpeg",
    "https://thumbs.dreamstime.com/b/belle-for%C3%AAt-tropicale-%C3%A0-l-itin%C3%A9raire-am%C3%A9nag%C3%A9-pour-amateurs-de-la-nature-de-ka-d-ang-36703721.jpg",
  ];
  // TODO: fetch proper backend endpoint
  return {
    plantName,
    roomName,
    pictures,
  };
}
