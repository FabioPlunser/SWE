<script lang="ts">
  type Picture = {
    imageRef: string;
    creationDate: Date;
  };

  import ImagesGrid from "./ImagesGrid.svelte";
  import Camera from "$lib/assets/icons/Camera.svg?component";

  export let data;

  let test: any, fileinput: any;

  function onFileSelected(e: any) {
    console.log(e);
    let image = e.target.files[0];
    let reader = new FileReader();
    reader.readAsDataURL(image);
    reader.onload = (e) => {
      // TODO: POST to endpoint and add image from response to data.pictures
      test = e?.target?.result;
      let response: Picture = {
        imageRef: "https://picsum.photos/200/300",
        creationDate: new Date(),
      };
      data.pictures.unshift(response);
      data.pictures = data.pictures;
    };
  }
</script>

<section class="mt-10">
  <div class="flex justify-between">
    <div class="text-2xl font-bold">
      <h1>Room: {data.roomName}</h1>
      <h1>Plant: {data.plantName}</h1>
    </div>
    <button
      on:click={() => fileinput.click()}
      class="btn btn-primary mt-3 hover:dark:fill-black hover:fill-white"
    >
      <Camera class="w-8 dark:fill-white" />
      <input
        class="hidden"
        type="file"
        accept=".jpg, .jpeg, .png; capture=camera"
        on:change={(e) => onFileSelected(e)}
        bind:this={fileinput}
      />
    </button>
  </div>

  <div>
    {#if test}
      <img src={test} alt="plant" />
    {/if}
  </div>

  <div class="mt-6">
    <ImagesGrid pictures={data.pictures} />
  </div>
</section>
