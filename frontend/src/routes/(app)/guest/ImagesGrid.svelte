<script lang="ts">
  type Picture = {
    imageRef: string;
    creationDate: Date;
  };

  export let pictures: Picture[];
</script>

<div class="grid grid-cols-2 sm:grid-cols-4 md:grid-cols-8 gap-4">
  {#each pictures as { imageRef, creationDate }}
    <div class="shrink-0 snap-center">
      <img
        alt="Plant"
        class="rounded-xl shadow-xl backdrop-blur-2xl"
        src={imageRef}
      />
      <h1 class="flex justify-center">
        Date: {creationDate.toLocaleDateString()}
      </h1>
    </div>
  {/each}
</div>
