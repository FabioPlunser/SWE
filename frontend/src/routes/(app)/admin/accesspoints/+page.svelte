<script lang="ts">
  export let data;
  $: console.log(data);
</script>

<section>
  {#if data.accessPoints}
    {#each data.accessPoints as accessPoint, i}
      <div class="card bg-base-100 shadow-xl w-fit p-24">
        <div>
          <h1>AccessPoint: {i}</h1>
          <h1>AccessPointId: {accessPoint.selfAssignedId}</h1>
          <h1>RoomName: {accessPoint.roomName}</h1>
          <p>ScanActive: {accessPoint.scanActive}</p>
          <p>Unlocked: {accessPoint.unlocked}</p>
          <p>connected: {accessPoint.connected}</p>
          <p>deleted: {accessPoint.deleted}</p>
        </div>
      </div>
    {/each}
  {/if}
</section>
