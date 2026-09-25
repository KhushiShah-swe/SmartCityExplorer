export default function Scene({ variant = 'city' }: { variant?: string }) {
  return (
    <svg
      viewBox="0 0 600 300"
      role="img"
      aria-label="Illustrated Chicago skyline"
      className={`scene scene-${variant.toLowerCase()}`}
    >
      <rect width="600" height="300" fill="currentColor" />
      <circle cx="455" cy="79" r="44" fill="#f4c57c" />
      <path d="M0 163Q160 120 290 170T600 144V300H0Z" fill="#c3d4c8" />
      <g fill="#315c68">
        <path d="M80 210V116h40v-20h24v114zM164 210V64h22V38h8v26h22v146zM226 210V120h46v90zM287 210V92h44v118zM342 210V136h39v74zM405 210V107h35v103zM462 210V143h53v67z" />
      </g>
      <g fill="#f7e7cb" opacity=".8">
        <path d="M176 83h6v9h-6zm19 0h6v9h-6zm-19 22h6v9h-6zm19 0h6v9h-6zm-19 22h6v9h-6zm19 0h6v9h-6zM299 109h7v12h-7zm15 0h7v12h-7zm-15 23h7v12h-7zm15 0h7v12h-7z" />
      </g>
      <path d="M0 217H600V300H0Z" fill="#82aeb5" />
      <path
        d="M0 237Q120 214 270 237T600 237M0 268Q170 244 345 267T600 267"
        fill="none"
        stroke="#d3e7e3"
        strokeWidth="3"
      />
      <path d="M0 203H600" stroke="#e9dbc6" strokeWidth="12" />
      <path
        d="M36 197v-31m-12 14 12-14 12 14M547 197v-31m-12 14 12-14 12 14"
        stroke="#315c68"
        strokeWidth="5"
        fill="none"
      />
    </svg>
  );
}
